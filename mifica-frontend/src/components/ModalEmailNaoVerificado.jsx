import React from 'react';

export default function ModalEmailNaoVerificado({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 rounded-xl shadow-2xl p-8 w-full max-w-md border border-slate-700">
        <div className="text-center mb-6">
          <h2 className="text-2xl md:text-3xl font-extrabold text-white mb-2">
            E-mail não verificado!
          </h2>
          <p className="text-sm md:text-base text-gray-300">
            Você precisa verificar seu e-mail para continuar
          </p>
        </div>

        <button
          onClick={onClose}
          className="w-full px-4 py-3 bg-white text-slate-900 rounded-md font-semibold hover:bg-gray-100 transition text-sm md:text-base"
        >
          Fechar
        </button>
      </div>
    </div>
  );
}
