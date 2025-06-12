import Modal from "react-modal"
import { useAppForm } from "@/config/use-app-form"
import { CreateReviewSchema } from "@/models/CreateReview"

type Props = {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: { rating: string; comment: string }) => void
}

export const AddReviewModal = ({ isOpen, onClose, onSubmit }: Props) => {
  const formData = useAppForm({
    defaultValues: { rating: "", comment: "" },
    validators: {
      onSubmit: () => {
        const values = formData.store.state.values
        const result = CreateReviewSchema.safeParse(values)
        if (!result.success) {
          const firstError = result.error.issues[0]
          return { isValid: false, error: new Error(firstError.message) }
        }
      },
    },
    onSubmit: ({ value }) => {
      onSubmit(value)
      onClose()
    },
  })

  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      contentLabel="Add Review"
      shouldCloseOnOverlayClick
      style={{
        overlay: {
          backgroundColor: "rgba(0,0,0,0.5)",
          backdropFilter: "blur(5px)",
          zIndex: 1100,
        },
        content: {
          maxWidth: "400px",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
        },
      }}
    >
      <h2 className="text-xl font-semibold mb-4">Add Review</h2>
      <formData.AppForm>
        <formData.FormContainer extraError={null} submitLabel="Submit Review">
          <formData.AppField name="rating">
            {(field) => (
              <field.SelectField
                label="Rating"
                options={Array.from({ length: 10 }, (_, i) => ({
                  value: (i + 1).toString(),
                  label: (i + 1).toString(),
                }))}
              />
            )}
          </formData.AppField>
          <formData.AppField name="comment">
            {(field) => <field.TextField label="Comment" />}
          </formData.AppField>
        </formData.FormContainer>
      </formData.AppForm>
    </Modal>
  )
}
