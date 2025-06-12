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
      console.log(value)
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
          backgroundColor: "rgba(0,0,0,0.4)",
          backdropFilter: "blur(6px)",
          zIndex: 1100,
        },
        content: {
          backgroundColor: "#0f1e11",
          color: "#f0f0f0",
          maxWidth: "fit-content",
          width: "fit-content",
          maxHeight: "fit-content",
          margin: "auto",
          borderRadius: "12px",
          padding: "24px",
          border: "1px solid #3a4d39",
          position: "relative",
        },
      }}
    >

      <h2 className="text-2xl font-bold mb-4">Add Review</h2>
      <formData.AppForm>
        <formData.FormContainer extraError={null} submitLabel="Submit Review">
          <formData.AppField name="rating">
            {(field) => (
              <field.SelectField
                label="Rating"
                options={Array.from({ length: 5 }, (_, i) => ({
                  value: (i + 1).toString(),
                  label: ("⭐".repeat(i + 1)).toString(),
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
