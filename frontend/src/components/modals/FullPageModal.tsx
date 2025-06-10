import { Dialog, Transition } from '@headlessui/react'
import { Fragment } from 'react'

export function FullPageModal({ isOpen, onClose, children }: {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
}) {
  return (
    <Transition appear show={isOpen} as={Fragment}>
      <Dialog as="div" className="relative z-50" onClose={onClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 backdrop-blur-sm bg-black/30" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-8">
            <Dialog.Panel className="w-full max-w-6xl transform rounded-xl bg-white p-8 shadow-xl transition-all max-h-[90vh] overflow-y-auto">
              <Dialog.Title className="text-2xl font-semibold mb-4">
                Título del Modal
              </Dialog.Title>
              {children}
              <div className="mt-6 flex justify-end">
                <button
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                  onClick={onClose}
                >
                  Cerrar
                </button>
              </div>
            </Dialog.Panel>
          </div>
        </div>
      </Dialog>
    </Transition>
  )
}